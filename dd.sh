#!/bin/bash

java -Djava.awt.headless=true -cp .:libs/*:sofia-web-server-0.1.jar net.cabezudo.sofia.core.WebServer -dd -i
