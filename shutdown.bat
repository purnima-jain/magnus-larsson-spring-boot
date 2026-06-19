@echo off

REM Docker Stuff - START
docker compose down
echo "Containers deleted!!!"
docker rmi magnus-larsson-spring-boot_review magnus-larsson-spring-boot_product magnus-larsson-spring-boot_product-composite magnus-larsson-spring-boot_recommendation
echo "Images deleted!!!"
REM Docker Stuff - END

REM Cleanup - START
rmdir /S /Q "api\target"
rmdir /S /Q "util\target"
rmdir /S /Q "microservices\product-service\target"
rmdir /S /Q "microservices\recommendation-service\target"
rmdir /S /Q "microservices\review-service\target"
rmdir /S /Q "microservices\product-composite-service\target"

echo "Target Folders deleted!!!"
REM Cleanup - END
