USER=kpipe
IMAGE_NAME=optimizer
IMAGE_TAG=$1
IMAGE=$USER/$IMAGE_NAME:$IMAGE_TAG
echo "===================================================================="
echo "  Building docker image $IMAGE"
echo "===================================================================="
docker build . -t $IMAGE
echo "===================================================================="
echo "  Logging in to dockerhub with user $USER"
echo "===================================================================="
docker login -u $USER -p $DOCKERHUB_PUSH_TOKEN
echo "===================================================================="
echo "  Pushing image $IMAGE_ID"
echo "===================================================================="
docker push $IMAGE
echo "Done"
