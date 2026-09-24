# iSpanProject-Forum

vue使用3.5.40
axios使用1.20.0
vuetify使用4.2.0
安裝websocket套件
npm install @stomp/stompjs

後端db連線設定至application-local.propties
gitgnore內已有設定略過該檔案
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=iSpan_Project;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=自己的密碼

// # 解決中文亂碼
spring.sql.init.encoding=UTF-8
