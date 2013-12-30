CURRENT=$1
NEXT=$2
SPACE=$3

cf space $SPACE

if [[ $SPACE == "staging" ]]; then
    cf map $NEXT staging spring.io
    cf unmap staging.spring.io $CURRENT
elif [[ $SPACE == "production" ]]; then
    cf map $NEXT spring.io
    cf map $NEXT www spring.io
    cf unmap .spring.io $CURRENT
    cf unmap www.spring.io $CURRENT
else
    echo "Invalid space: [$SPACE]"
fi
