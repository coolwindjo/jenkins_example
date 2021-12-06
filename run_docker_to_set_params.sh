echo $1
docker run --rm \
-v ${PWD}:/usr/src/app \
-v $1:/usr/src/app/workspace \
python3-docker \
python set_param_yaml.py workspace/store_param.yaml $2 $3