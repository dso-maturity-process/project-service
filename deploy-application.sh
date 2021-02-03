#!/bin/bash

kubectl apply -f minikube-deployment.yaml

kubectl rollout restart deployment/project-service-app -n dmp
