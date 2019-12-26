docker images |grep chatroomremote |sed -n "3,+100p" |awk '{print $3}' | xargs docker rmi
