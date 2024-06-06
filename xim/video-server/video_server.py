import logging
from flask import Flask, request, send_from_directory, jsonify
import subprocess
import os
import uuid
import shutil

app = Flask(__name__)

logging.basicConfig(level=logging.DEBUG)

DOWNLOAD_FOLDER = '/root/data'
DIRECTORIES = {
    'sample': '/root/sample',
    'data': DOWNLOAD_FOLDER,
}

def clear_directory(directory):
    for item in os.listdir(directory):
        item_path = os.path.join(directory, item)
        if os.path.isfile(item_path) or os.path.islink(item_path):
            os.unlink(item_path)
        elif os.path.isdir(item_path):
            shutil.rmtree(item_path)

@app.route('/api/videosvr', methods=['POST'])
def merge_videos():
    data = request.get_json()
    app.logger.info('Received request to merge videos with data: %s', data)
    url1 = data.get('url1')
    url2 = data.get('url2')

    unique_filename = str(uuid.uuid4())
    clear_directory(DOWNLOAD_FOLDER)
    src1 = os.path.join(DOWNLOAD_FOLDER, f"{unique_filename}_video1.mp4")
    src2 = os.path.join(DOWNLOAD_FOLDER, f"{unique_filename}_video2.mp4")
    dst = os.path.join(DOWNLOAD_FOLDER, f"{unique_filename}_merged.mp4")

    download_video(url1, src1)
    download_video(url2, src2)
    app.logger.info('Video merging...')
    subprocess.call(["/root/ffmpeg", "-i", src1, "-i", src2, "-filter_complex",
                     "[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1", "-vsync", "vfr", dst])
    download_url = f"http://{request.host}/download/data/{os.path.basename(dst)}"
    app.logger.info('Video merged successfully, download URL: %s', download_url)
    return jsonify({'download_url': download_url})

@app.route('/download/<directory_alias>/<filename>', methods=['GET'])
def download_file(directory_alias, filename):
    if directory_alias in DIRECTORIES:
        directory_path = DIRECTORIES[directory_alias]
        if '..' in filename or filename.startswith('/'):
            abort(404)
        return send_from_directory(directory_path, filename, as_attachment=True)
    else:
        abort(404)

def download_video(url, path):
    subprocess.call(['wget', '-O', path, url])

if __name__ == '__main__':
    if not os.path.exists(DOWNLOAD_FOLDER):
        os.makedirs(DOWNLOAD_FOLDER)
    app.run(debug=True, host='0.0.0.0', port=7070)


