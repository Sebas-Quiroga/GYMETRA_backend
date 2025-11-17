pipeline {
    agent any

    environment {
        DOCKER_IMAGE_PREFIX = 'develop-'
        DOCKER_REGISTRY = 'your-registry.com' // Replace with your Docker registry
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build GYMETR-login Backend') {
            steps {
                dir('backend/GYMETR-login') {
                    sh 'docker build -t ${DOCKER_IMAGE_PREFIX}gymetr-login:latest .'
                }
            }
        }

        stage('Build GYMETR-Membership Backend') {
            steps {
                dir('backend/GYMETR-Membership') {
                    sh 'docker build -t ${DOCKER_IMAGE_PREFIX}gymetr-membership:latest .'
                }
            }
        }

        stage('Build GYMETRA - Qr Backend') {
            steps {
                dir('backend/GYMETRA - Qr') {
                    sh 'docker build -t ${DOCKER_IMAGE_PREFIX}gymetra-qr:latest .'
                }
            }
        }

        stage('Build admin-frontend') {
            steps {
                dir('frontend/admin-frontend') {
                    sh 'docker build -t ${DOCKER_IMAGE_PREFIX}admin-frontend:latest .'
                }
            }
        }

        stage('Build gymetra-frontend') {
            steps {
                dir('frontend/gymetra-frontend') {
                    sh 'docker build -t ${DOCKER_IMAGE_PREFIX}gymetra-frontend:latest .'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down'
                sh 'docker-compose up -d --build'
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
        }
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}
