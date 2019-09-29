if [[ -n $1 ]]; then
  helm template --name "$1" --output-dir target vertx-delay-service
  else
    helm template --name test-v1 --output-dir target vertx-delay-service
fi

