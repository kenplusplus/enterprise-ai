import base64
import os
from typing import List, Union

import numpy as np
import torch  # type: ignore
import torch.nn.functional as F  # type: ignore
import transformers  # type: ignore
from llmspec import EmbeddingData, EmbeddingRequest, EmbeddingResponse, TokenUsage

from mosec import ClientError, Runtime, Server, Worker

import intel_extension_for_pytorch as ipex
#from optimum.intel import IPEXModel

DEFAULT_MODEL = "/root/bge-large-zh/"


class Embedding(Worker):
    def __init__(self):
        self.model_name = os.environ.get("EMB_MODEL", DEFAULT_MODEL)
        self.tokenizer = transformers.AutoTokenizer.from_pretrained(self.model_name)
        self.model = transformers.AutoModel.from_pretrained(self.model_name)
        self.device = (
            torch.cuda.current_device() if torch.cuda.is_available() else "cpu"
        )
        #self.model = IPEXModel.from_pretrained(self.model_name, torch_dtype=torch.bfloat16)

        self.model = self.model.to(self.device)
        self.model.eval()

        # jit trace model
        self.model = ipex.optimize(self.model, dtype=torch.bfloat16)
        vocab_size = self.model.config.vocab_size
        batch_size = 16
        seq_length = 512
        d = torch.randint(vocab_size, size=[batch_size, seq_length])
        t = torch.randint(0, 1, size=[batch_size, seq_length])
        m = torch.randint(1, 2, size=[batch_size, seq_length])
        self.model = torch.jit.trace(self.model, [d, t, m], check_trace=False, strict=False)
        self.model = torch.jit.freeze(self.model)

    def get_embedding_with_token_count(
        self, sentences: Union[str, List[Union[str, List[int]]]]
    ):
        # Mean Pooling - Take attention mask into account for correct averaging
        def mean_pooling(model_output, attention_mask):
            # First element of model_output contains all token embeddings
            token_embeddings = model_output['last_hidden_state']
            input_mask_expanded = (
                attention_mask.unsqueeze(-1).expand(token_embeddings.size()).float()
            )
            return torch.sum(token_embeddings * input_mask_expanded, 1) / torch.clamp(
                input_mask_expanded.sum(1), min=1e-9
            )

        # Tokenize sentences
        # TODO: support `List[List[int]]` input
        encoded_input = self.tokenizer(
            sentences, padding=True, truncation=True, return_tensors="pt"
        )
        inputs = encoded_input.to(self.device)
        token_count = inputs["attention_mask"].sum(dim=1).tolist()[0]
        # Compute token embeddings
        model_output = self.model(**inputs)
        # Perform pooling
        sentence_embeddings = mean_pooling(model_output, inputs["attention_mask"])
        # Normalize embeddings
        sentence_embeddings = F.normalize(sentence_embeddings, p=2, dim=1)

        return token_count, sentence_embeddings

    def deserialize(self, data: bytes) -> EmbeddingRequest:
        return EmbeddingRequest.from_bytes(data)

    def serialize(self, data: EmbeddingResponse) -> bytes:
        return data.to_json()

    def forward(self, data: EmbeddingRequest) -> EmbeddingResponse:
        if data.model != self.model_name:
            raise ClientError(
                f"the requested model {data.model} is not supported by "
                f"this worker {self.model_name}"
            )
        token_count, embeddings = self.get_embedding_with_token_count(data.input)
        embeddings = embeddings.detach()
        if self.device != "cpu":
            embeddings = embeddings.cpu()
        embeddings = embeddings.numpy()
        if data.encoding_format == "base64":
            embeddings = [
                base64.b64encode(emb.astype(np.float32).tobytes()).decode("utf-8")
                for emb in embeddings
            ]
        else:
            embeddings = [emb.tolist() for emb in embeddings]

        resp = EmbeddingResponse(
            data=[
                EmbeddingData(embedding=emb, index=i)
                for i, emb in enumerate(embeddings)
            ],
            model=self.model_name,
            usage=TokenUsage(
                prompt_tokens=token_count,
                # No completions performed, only embeddings generated.
                completion_tokens=0,
                total_tokens=token_count,
            ),
        )
        return resp


if __name__ == "__main__":
    server = Server()
    emb = Runtime(Embedding)
    server.register_runtime(
        {
            "/v1/embeddings": [emb],
            "/embeddings": [emb],
        }
    )
    server.run()
