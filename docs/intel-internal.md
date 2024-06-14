# Introduce Configurations for Intel Internal


## 1. Configure Intel NTP Server

- Background

It is must-to-have use Intel NTP server for time sync.

- Solution

RHEL/CentOS Stream will install chronyd service by default, On ubuntu/debian, you need install it manually "sudo apt install chrony"
Add Intel server in /etc/chrony/chrony.conf

```
pool corp.intel.com iburst
```

## 2. Install Intel Certificates

- Background

You need install Intel Certificates to acess Intel resources like container registry, besclient etc.

- Solution

1. Download the Intel Certificates

```
wget -e use_proxy=no http://certs.intel.com/repository/certificates/Intel%20Root%20Certificate%20Chain%20Base64.zip
wget -e use_proxy=no http://certs.intel.com/crt/IntelSHA2RootChain-Base64.zip
```

2. Install certificate

   - Ubuntu and Debian

```
sudo mkdir -p  /usr/local/share/ca-certificates
sudo unzip ~/"Intel Root Certificate Chain Base64.zip" -d /usr/local/share/ca-certificates
sudo unzip ~/"IntelSHA2RootChain-Base64.zip" -d /usr/local/share/ca-certificates
sudo update-ca-certificates
sudo c_rehash
```

   - CentOS and Red Hat

```
sudo unzip ~/"Intel Root Certificate Chain Base64.zip" -d /etc/pki/ca-trust/source/anchors
sudo unzip ~/"IntelSHA2RootChain-Base64.zip" -d /etc/pki/ca-trust/source/anchors
sudo update-ca-trust force-enable
sudo update-ca-trust extract
```

## 3. Add registry mirror for containerd

- Background

docker.io or k8s.io are very slow in sometime or blocked by PRC.

- Solution

Add following content into /etc/containerd/config.toml

```
disabled_plugins = []
version = 2

[plugins."io.containerd.grpc.v1.cri".registry.mirrors]

        [plugins."io.containerd.grpc.v1.cri".registry.mirrors."docker.io"]
          endpoint = ["https://bqr1dr1n.mirror.aliyuncs.com", "https://hub-mirror.c.163.com"]

        [plugins."io.containerd.grpc.v1.cri".registry.mirrors."gcr.io"]
          endpoint = ["https://gcr.mirrors.ustc.edu.cn"]

        [plugins."io.containerd.grpc.v1.cri".registry.mirrors."k8s.gcr.io"]
          endpoint = ["https://registry.cn-hangzhou.aliyuncs.com/google_containers/"]
```

## 4. Add Intel proxy for docker build

- Background

When run `docker build`, the docker application itself need proxy to download container images.

- Solution

Create proxy.conf at `/etc/systemd/system/docker.service.d/proxy.conf` with following content

```
[Service]
Environment="HTTP_PROXY=http://child-prc.intel.com:913"
Environment="HTTPS_PROXY=http://child-prc.intel.com:913"
Environment="NO_PROXY=localhost,.intel.com,intel.com"
```

## 5. Add Intel proxy for running container launched by docker

- Background

If a container launched by a docker, the application within container requires Intel proxy.

- Solution

1. Solution 1

When launch container via docker, you can pass proxy via `docker run -e http_proxy -e https_proxy`

2. Solution 2

You can create ~/.docker/config.json for current user with following content:

```
{
	"proxies": {
		"default": {
			"httpProxy": "http://child-prc.intel.com:913",
			"httpsProxy": "http://child-prc.intel.com:913",
			"noProxy": "127.0.0.0/8,*.intel.com"
		}
	}
}
```

## 6. Add Intel proxy for running container on Kubernetes

- Background

All container on Kubernetes within Intel network need proxy.

- Solution

Add `http_proxy, https_proxy, no_proxy` into `kube-apiserver.yaml`
```
    env:
    - name: http_proxy
      value: http://child-prc.intel.com:913/
    - name: https_proxy
      value: http://child-prc.intel.com:913/
    - name: no_proxy
      value: localhost,127.0.0.1,.intel.com,.svc,192.168.0.0/16,10.0.0.0/8,172.10.0.0/16,172.11.0.0/16
    image: registry.k8s.io/kube-apiserver:v1.25.3

```
