pipeline {
    agent any

    environment {
        DB_URL = 'jdbc:mysql://192.168.198.1:3306/piDB?createDatabaseIfNotExist=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC'
        DB_USERNAME = 'root'
        DB_PASSWORD = ''
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
                sh """
                    mvn test \
                    -Dspring.datasource.url="${DB_URL}" \
                    -Dspring.datasource.username=${DB_USERNAME} \
                    -Dspring.datasource.password=${DB_PASSWORD}
                """
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
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
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Lancement de l\'application...'
                sh 'java -jar target/*.jar &'
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
