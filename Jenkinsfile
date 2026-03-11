pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        ANDROID_HOME = '/opt/android-sdk'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        GITHUB_TOKEN = credentials('github-token')
        REPO_NAME = 'YngY/ComposeLearning'
        GIT_AUTHOR_NAME = 'Jenkins'
        GIT_AUTHOR_EMAIL = 'jenkins@localhost'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "https://github.com/${REPO_NAME}.git"
            }
        }

        stage('Setup') {
            steps {
                sh '''
                    java -version
                    echo "JAVA_HOME=${JAVA_HOME}"
                    echo "ANDROID_HOME=${ANDROID_HOME}"
                    
                    # 接受 Android SDK 许可证
                    yes | sdkmanager --licenses || true
                '''
            }
        }

        stage('Build Debug APK') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean assembleDebug --stacktrace
                '''
            }
        }

        stage('Copy APK to src') {
            steps {
                sh '''
                    # 创建存放 APK 的目录
                    mkdir -p src/apk
                    
                    # 复制 APK 到 src 目录
                    cp app/build/outputs/apk/debug/app-debug.apk src/apk/
                    
                    # 查看生成的 APK
                    ls -la src/apk/
                '''
            }
        }

        stage('Commit and Push') {
            steps {
                sh '''
                    cd /home/jenkins/workspace/ComposeLearning-AutoBuild
                    
                    # 配置 Git
                    git config --global user.name "${GIT_AUTHOR_NAME}"
                    git config --global user.email "${GIT_AUTHOR_EMAIL}"
                    
                    # 添加 APK 文件
                    git add src/apk/app-debug.apk
                    
                    # 检查是否有文件需要提交
                    if git diff --cached --quiet; then
                        echo "No changes to commit"
                    else
                        # 提交更改
                        git commit -m "🤖 Jenkins: Build APK v1.0.0-${BUILD_NUMBER}"
                        
                        # 推送到 GitHub
                        git push https://${GITHUB_TOKEN}@github.com/${REPO_NAME}.git main
                    fi
                '''
            }
        }
    }

    post {
        success {
            echo "✅ 构建成功！APK 已提交到 GitHub"
        }
        failure {
            echo "❌ 构建失败！"
        }
        always {
            cleanWs()
        }
    }
}
