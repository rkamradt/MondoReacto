pipeline {
  agent {
    kubernetes {
      label 'mondo-reacto'
      idleMinutes 5
      yamlFile 'build-pod.yaml'
      defaultContainer 'maven'  
    }
  }
  stages {
    stage('Build All') {
      steps {
        sh "mvn clean install"
      }
    }
    stage('Build Docker Image For Incoming Persist') {
      steps {
        sh "cd incoming-persist;mvn jib:build"
      }
    }
    stage('Build Docker Image For Incoming Persist Test') {
      steps {
        sh "cd incoming-persist-test;mvn jib:build"
      }
    }
    stage('Build Docker Image For Incoming Ingestion Service') {
      steps {
        sh "cd incoming-service;mvn jib:build"
      }
    }
    stage('Build Docker Image For Incoming Ingestion Service Test') {
      steps {
        sh "cd incoming-service-test;mvn jib:build"
      }
    }
    stage('Build Docker Image For Incoming Read Service') {
      steps {
        sh "cd incoming-read-service;mvn jib:build"
      }
    }
    stage('Build Docker Image For Incoming Read Service Test') {
      steps {
        sh "cd incoming-read-service-test;mvn jib:build"
      }
    }
  }
}
