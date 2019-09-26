if [[ -n $1 ]]; then
  helm template --name "$1" --output-dir target vertx-rest-service
  else
    helm template --name test-v1 --output-dir target vertx-rest-service
fi

