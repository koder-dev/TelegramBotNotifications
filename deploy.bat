
pushd ~\TelegramBotNotifications\

call git checkout master
call git pull origin master

call docker compose -f docker-compose.yml --env-file .env down --timeout=60 --remove-orphans
call docker compose -f docker-compose.yml --env-file .env up --build --detach

popd