RUN apt-get update && apt-get install -y \
    package1 \
    package2=1.2.3 \
    package3 \
 && rm -rf /var/lib/apt/lists/*
