gcloud auth list
if [ $? -ne 0 ]
then
   exit 1
fi
exit 0
