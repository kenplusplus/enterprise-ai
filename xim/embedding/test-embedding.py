"""OpenAI embedding client example."""

from openai import Client

DEFAULT_MODEL = "/root/bge-large-zh/"
SERVICE_URL="http://127.0.0.1:8000"
INPUT_STR="Hello world!"

client = Client(api_key="fake", base_url=SERVICE_URL)
emb = client.embeddings.create(
    model=DEFAULT_MODEL,
    input=INPUT_STR,
)

print(len(emb.data))  # type: ignore
print(emb.data[0].embedding)  # type: ignore
