# Continuous Integration - Docker

## Docker

### Build Docker image

```bash
docker build -t [Image Name] 
```

### Push image to dockerhub

```bash
docker push [Image Name]
```

### Pull image

```bash
docker pull [Image Name]
```

### List all images

```bash
docker images
docker image ps
```

### Create a container

```bash
docker run -d [Image Name] -p [local port]:[container port] -v [local folder]:[container folder] --restart always
```

### List all containers

```bash
docker ps
docker ps -a
```

### Stop Container

```bash
docker stop [Container Id]
```

### Start

```bash
docker start [Container Id]
```

### Bash in container

```bash
docker exec -it [Container id] bash
```

### Copy file to container

```bash
docker cp [file] [Container Id]:[Container Folder]
```

## docker-compose

### Launch

```bash
docker-compose up -d
```

### Stop

```bash
docker-compose down
```
