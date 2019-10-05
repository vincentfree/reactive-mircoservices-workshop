location=spring-rest-service
if [[ -n $1 && -z $2 ]]; then
  helm template --name "$1" --output-dir target $location
  elif [[ -n $1 && -n $2 ]]; then
    helm template --name "$1" --set image.tag="$2" --output-dir target $location
  else
    helm template --name test-v1 --output-dir target $location
fi
