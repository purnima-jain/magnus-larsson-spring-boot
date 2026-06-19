@echo off

REM Cleanup - START
rmdir /S /Q "api\target"
rmdir /S /Q "util\target"
rmdir /S /Q "microservices\product-service\target"
rmdir /S /Q "microservices\recommendation-service\target"
rmdir /S /Q "microservices\review-service\target"
rmdir /S /Q "microservices\product-composite-service\target"
REM Cleanup - END

REM Building Libraries - START

cd "api"
call mvn clean install
echo "api library build complete!!!"
cd ..

cd "util"
call mvn clean install
echo "util library build complete!!!"
cd ..

REM Building Libraries - END

REM Building Microservices - START
cd "microservices"

cd "product-service"
call mvn clean install
echo "product-service build complete!!!"
cd ..

cd "recommendation-service"
call mvn clean install
echo "recommendation-service build complete!!!"
cd ..

cd "review-service"
call mvn clean install
echo "review-service build complete!!!"
cd ..

cd "product-composite-service"
echo The current working directory is: %CD%
call mvn clean install
echo "product-composite-service build complete!!!"
cd ..

REM Building Microservices - END

REM Docker Stuff - START
cd ..
echo The current working directory is: %CD%

docker compose build
echo "Images build complete!!!!"

docker compose up

REM Docker Stuff - END
