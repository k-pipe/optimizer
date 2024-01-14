FROM gcr.io/google.com/cloudsdktool/google-cloud-cli:432.0.0-alpine AS build
RUN gcloud components install gke-gcloud-auth-plugin

# matching python version
FROM python:3.10.11-alpine
#
# Copy glcoud cli and kubectl
#
COPY --from=build /google-cloud-sdk /google-cloud-sdk
ENV PATH=/google-cloud-sdk/bin:$PATH
ENV CLOUD_SDK_VERSION=432.0.0
#
# Copy optimizer
#
COPY  --from=kpipe/optimizer /bin/optimizer /bin/
COPY test-gcloud.sh /
WORKDIR  /google-cloud-sdk
RUN find . -name __pycache__  | sed "s#^#rm -rf #" | sh
RUN optimizer /bin/sh /test-gcloud.sh