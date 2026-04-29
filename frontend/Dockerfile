FROM node:20-alpine AS build
WORKDIR /app
COPY . .
RUN npm install

ENTRYPOINT ["npm", "run", "dev", "--", "--host"]
