FROM node:10
WORKDIR /app

# Copy root package files
COPY package.json yarn.lock lerna.json tsconfig.json tsconfig.settings.json ./

# Copy workspace package files
COPY example/package.json example/
COPY example/tsconfig.json example/

# Install dependencies
RUN yarn install --ignore-scripts

# Copy source code
COPY . .

# Build (compile and bundle)
RUN yarn prepare

# Expose port
EXPOSE 8030

# Start
CMD ["yarn", "start"]
