@echo off

REM Docker Stuff - START
docker compose down
docker rmi magnus-larsson-spring-boot_review magnus-larsson-spring-boot_product magnus-larsson-spring-boot_product-composite magnus-larsson-spring-boot_recommendation
REM Docker Stuff - END
