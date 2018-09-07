# mybatis generator 个性化版本

> 本项目是针对 [官方项目 1.3.7版](https://github.com/mybatis/generator/tree/mybatis-generator-1.3.7) 针对个人习惯等做了一些自定义的配置, 但是要搭配[合理的配置文件](generatorTest.xml)才能得到想要的效果, 其中关键就是一定 **`不能取消注释`**, 合并代码的关键在与注释中的 `@mbg.generated` 如果注释中有这个, 合并的时候就会去掉旧文件中的代码, 只保留新文件中的, 所以, 注释中有这个的代码千万 **`不能修改`**, 否则以后重新生成代码的时候就会覆盖掉之前的修改, 具体的改动如下:
- 修改注释的内容, 去掉一下繁杂的不必要的内容, 是内容更简洁
- 去掉`Getter`、`Setter` 方法, 用`lombok`中的 `@Getter`、`@Setter`两个注解代替, 是代码更简洁
- 由于我使用swagger自动生成文档, 所以我给实体类中每个字段加上注解`@ApiModelProperty("")`, 里面的内容为数据库中的`comment`
- 由于项目默认不会合并java代码, 这里我根据注释中的`@mbg.generated` 等特殊注解做判断, 把旧的文件中没有这个注解成员添加到新生成的文件中. 对于 `import` 的策略是直接去重合并
- 把mybatis直接打入包中, 配置文件中就不必再配置`mysql-connector-java`包的路径

## usage:
   
  1. 下载 [mybatis-generator-core-1.3.7.jar](target/mybatis-generator-core-1.3.7.jar)
  
  2. 手动添加 JAR 包到本地仓库, 切换到jar包所在目录运行
      ```
      mvn install:install-file -Dfile=mybatis-generator-core-1.3.7.jar -DgroupId=org.mybatis.generator -DartifactId=mybatis-generator-core -Dversion=1.3.7 -Dpackaging=jar
      ```
  3. 下载包括依赖的[mybatis-generator-core-1.3.7-jar-with-dependencies.jar](target/mybatis-generator-core-1.3.7-jar-with-dependencies.jar) (由于原来的jar包中少了一些依赖, 会导致运行不成功, 所以需要此操作)
  
  4. 到`maven`仓库删除`mybatis-generator-core-1.3.7.jar` 并且 把`mybatis-generator-core-1.3.7-jar-with-dependencies.jar`改名为`mybatis-generator-core-1.3.7.jar`
  
  5. 至此, mybatis generator 安装完成, 把[配置文件](generatorTest.xml)拷贝到你的项目目录, 修改相应的配置, 添加`mybatis-generator-maven-plugin`插件即可
  
  
  
  
  
    
                                     
