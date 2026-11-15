
pipeline {
    agent any

    stages {

        stage('Git Checkout') {
            steps {
                echo 'Source code fetched from GitHub'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application'
                bat 'mvn -version'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing application'
                bat 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image'
                bat 'docker build -t online-exam:latest .'
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }

        failure {
            echo 'Pipeline execution failed!'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
