#!/bin/bash
set -e

# Function to create a database if it doesn't exist
create_database() {
  local database=$1
  echo "Creating database '$database'"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "$database";
    GRANT ALL PRIVILEGES ON DATABASE "$database" TO "$POSTGRES_USER";
EOSQL
}

# Create databases from environment variable
if [ -n "$CREATE_DATABASES" ]; then
  echo "Creating databases: $CREATE_DATABASES"
  
  # Split the CREATE_DATABASES variable by comma or space
  for db in $(echo $CREATE_DATABASES | tr ',' ' '); do
    create_database $db
  done
  
  echo "Database creation completed"
fi