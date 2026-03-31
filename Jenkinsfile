pipeline {
    agent any

    environment {
        DOCKERHUB_IMAGE = 'VOTRE_USERNAME/streetleague-app'
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -Dspring.datasource.url="jdbc:mysql://192.168.198.1:3306/piDB?createDatabaseIfNotExist=true&useUnicode=true&serverTimezone=UTC" -Dspring.datasource.username=root -Dspring.datasource.password=""'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
                success {
                    echo '✅ Tests passés avec succès.'
                }
                failure {
                    echo '❌ Tests échoués — consultez les rapports Surefire.'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKERHUB_IMAGE}:latest ."
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKERHUB_IMAGE}:latest"
                }
            }
        }

        stage('Docker Run') {
            steps {
                sh 'docker stop streetleague-app || true'
                sh 'docker rm streetleague-app || true'
                sh """
                    docker run -d \
                    --name streetleague-app \
                    -p 8085:8085 \
                    -e SPRING_DATASOURCE_URL="jdbc:mysql://192.168.198.1:3306/piDB?createDatabaseIfNotExist=true&useUnicode=true&serverTimezone=UTC" \
                    -e SPRING_DATASOURCE_USERNAME=root \
                    -e SPRING_DATASOURCE_PASSWORD="" \
                    ${DOCKERHUB_IMAGE}:latest
                """
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Pipeline échoué. Vérifiez les logs ci-dessus.'
        }
        always {
            cleanWs()
        }
    }
}
