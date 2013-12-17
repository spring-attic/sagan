CURRENT=$1
NEXT=$2
cf map $NEXT spring.io
cf map $NEXT www spring.io
cf unmap .spring.io $CURRENT
cf unmap www.spring.io $CURRENT
