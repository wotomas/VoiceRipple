clear
echo "You are going to upload your library to bintray?"
select yn in "Yes" "No"; do
    case $yn in
        Yes )
            echo "Please Type your bintray username: "
            read username

            echo "Please Type your bintray API Key: "
            read apiKey

            echo "Hello, $username with API key $apiKey uploading to bintray"
            echo "==================================== Running Script ================================================="
            echo "#                                                                                                   #"
            echo "#                                                                                                   #"
            echo "  ./gradlew clean build bintrayUpload -PbintrayUser=$username -PbintrayKey=$apiKey -PdryRun=false  "
            echo "#                                                                                                   #"
            echo "#                                                                                                   #"
            echo "====================================================================================================="
            eval './gradlew clean build bintrayUpload -PbintrayUser=$username -PbintrayKey=$apiKey -PdryRun=false'
            break;;
        No )
            exit;;
    esac
done