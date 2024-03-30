import os
import requests
import logging
import argparse

"""
curl -F "file=@/path/to/file" http://localhost:5000/whisper
"""

def parse_args():
    parser = argparse.ArgumentParser(
                    prog='whisper_client',
                    description='Client to test whisper server')
    parser.add_argument('-f', '--file', dest="audio_file", required=True)
    parser.add_argument('-u', '--url', dest="server_url", default="http://localhost:5000/whisper")
    args = parser.parse_args()
    return args

def send_requests(filepath, server_url):
    files = {
        'file': open(filepath, 'rb'),
    }
    response = requests.post(server_url, files=files)
    print("HTTP Response", response.json())
    results = response.json()
    return results['text']

if __name__=="__main__":
    logging.basicConfig()
    logging.getLogger().setLevel(logging.DEBUG)

    args = parse_args()
    filepath = os.path.realpath(args.audio_file)
    assert filepath, "audio file %s not exists" % filepath

    print("======================================")
    print(" Whisper Test Client: ")
    print(" Audio file:      ", filepath)
    print(" Server URL:      ", args.server_url)
    print("======================================")
    data = send_requests(filepath, args.server_url)
    print(data)
