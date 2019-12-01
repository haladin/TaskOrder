# Task ordering service

Task ordering service using Kotlin with Ktor framework

## Pre-requisite for building and running: 

- OpenJDK (on Ubuntu/Debian based distros `sudo apt install default-jdk`)

## Build:

```bash
./gradlew build
```

## Run: 
    
```bash
java -server -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -jar ./build/libs/sumup_task-0.0.1-all.jar
```
    
## Usage:

Example input:

```json
{
    "tasks": [
        {
            "name": "task-1",
            "command": "touch /tmp/file1"
        },
        {
            "name": "task-2",
            "command": "cat /tmp/file1",
            "requires": [
                "task-3"
            ]
        },
        {
            "name": "task-3",
            "command": "echo 'Hello World!' > /tmp/file1",
            "requires": [
                "task-1"
            ]
        },
        {
            "name": "task-4",
            "command": "rm /tmp/file1",
            "requires": [
                "task-2",
                "task-3"
            ]
        }
    ]
}
```
To get json output from the service add "Accept: application/json" header to http request:

```bash
curl -d @./input.json -H "Content-Type: application/json" -H "Accept: application/json" -L http://localhost:8080
```
    
Example output:
```json
[ {
  "name" : "task-1",
  "command" : "touch /tmp/file1"
}, {
  "name" : "task-3",
  "command" : "echo 'Hello World!' > /tmp/file1"
}, {
  "name" : "task-2",
  "command" : "cat /tmp/file1"
}, {
  "name" : "task-4",
  "command" : "rm /tmp/file1"
} ]
``` 
    
To get raw output from the service run:
    
```bash
curl -d @./input.json -H "Content-Type: application/json" -L http://localhost:8080
```
    
Example raw output:
    
```bash
#!/usr/bin/env bash

touch /tmp/file1
echo 'Hello World!' > /tmp/file1
cat /tmp/file1
rm /tmp/file1
```
