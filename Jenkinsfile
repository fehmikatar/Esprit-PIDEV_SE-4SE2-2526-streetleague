pipeline {
    agent any

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

        stage('Run') {
            steps {
                echo '🚀 Lancement de l\'application...'
                sh 'nohup java -jar target/*.jar --spring.datasource.url="jdbc:mysql://192.168.198.1:3306/piDB?createDatabaseIfNotExist=true&useUnicode=true&serverTimezone=UTC" --spring.datasource.username=root --spring.datasource.password="" &'
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
