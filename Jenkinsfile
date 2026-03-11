pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        ANDROID_HOME = '/opt/android-sdk'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        GITHUB_TOKEN = credentials('github-token')
        REPO_NAME = 'YngY/ComposeLearning'
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
                    
                    # 安装必要的 SDK 组件
                    sdkmanager "platforms;android-34"
                    sdkmanager "build-tools;34.0.0"
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

        stage('Build Release APK') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew assembleRelease --stacktrace
                '''
            }
        }

        stage('Create GitHub Release') {
            steps {
                script {
                    def version = "v1.0.0-${env.BUILD_NUMBER}"
                    
                    // 创建 Release
                    sh """
                        curl -X POST \
                        https://api.github.com/repos/${REPO_NAME}/releases \
                        -H "Authorization: token ${GITHUB_TOKEN}" \
                        -H "Accept: application/vnd.github.v3+json" \
                        -d '{
                            "tag_name": "${version}",
                            "name": "ComposeLearning ${version}",
                            "body": "自动构建版本 ${version}\\n\\n构建号: ${env.BUILD_NUMBER}\\n\\n包含内容:\\n- 20+ Jetpack 组件示例\\n- 完整可运行代码",
                            "draft": false,
                            "prerelease": false
                        }'
                    """
                }
            }
        }

        stage('Upload APK to Release') {
            steps {
                script {
                    def version = "v1.0.0-${env.BUILD_NUMBER}"
                    
                    // 上传 Debug APK
                    sh """
                        curl -X POST \
                        "https://uploads.github.com/repos/${REPO_NAME}/releases/tags/${version}/assets?name=app-debug.apk" \
                        -H "Authorization: token ${GITHUB_TOKEN}" \
                        -H "Content-Type: application/vnd.android.package-archive" \
                        --data-binary "@app/build/outputs/apk/debug/app-debug.apk"
                    """
                    
                    // 上传 Release APK (如果存在)
                    sh """
                        if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
                            curl -X POST \
                            "https://uploads.github.com/repos/${REPO_NAME}/releases/tags/${version}/assets?name=app-release.apk" \
                            -H "Authorization: token ${GITHUB_TOKEN}" \
                            -H "Content-Type: application/vnd.android.package-archive" \
                            --data-binary "@app/build/outputs/apk/release/app-release.apk"
                        fi
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ 构建成功！"
            echo "📦 APK 已上传到 GitHub Releases"
        }
        failure {
            echo "❌ 构建失败！"
        }
        always {
            cleanWs()
        }
    }
}
