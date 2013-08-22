#!/bin/bash -x


cf switch-space production || exit

cf map sagan blog interface21.com &
cf map sagan blog springsource.com &
cf map sagan blog springsource.org &
cf map sagan interface21.com &
cf map sagan spring.io &
cf map sagan springframework.io &
cf map sagan springframework.org &
cf map sagan springsource.com &
cf map sagan springsource.org &
cf map sagan static springframework.org
cf map sagan static springsource.org
cf map sagan www interface21.com &
cf map sagan www spring.io &
cf map sagan www springframework.io &
cf map sagan www springframework.org &
cf map sagan www springsource.com &
cf map sagan www springsource.org &
