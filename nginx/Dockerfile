FROM nginx:latest

# Remove the default Nginx configuration file
RUN rm /etc/nginx/conf.d/default.conf

# Copy the custom configuration file
COPY conf.d/default.conf /etc/nginx/conf.d/

# Expose port 80
EXPOSE 80