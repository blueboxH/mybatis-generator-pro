/**
 *    Copyright 2006-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal;

import static org.mybatis.generator.api.dom.OutputUtilities.newLine;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.*;
import java.util.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.ShellException;

/**
 * The Class DefaultShellCallback.
 *
 * @author Jeff Butler
 */
public class DefaultShellCallback implements ShellCallback {

    /**
     * The overwrite.
     */
    private boolean overwrite;

    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite the overwrite
     */
    public DefaultShellCallback(boolean overwrite) {
        super();
        this.overwrite = overwrite;
    }

    /* (non-Javadoc)
     * @see org.mybatis.generator.api.ShellCallback#getDirectory(java.lang.String, java.lang.String)
     */
    @Override
    public File getDirectory(String targetProject, String targetPackage)
            throws ShellException {
        // targetProject is interpreted as a directory that must exist
        //
        // targetPackage is interpreted as a sub directory, but in package
        // format (with dots instead of slashes). The sub directory will be
        // created
        // if it does not already exist

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            throw new ShellException(getString("Warning.9", //$NON-NLS-1$
                    targetProject));
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new ShellException(getString("Warning.10", //$NON-NLS-1$
                        directory.getAbsolutePath()));
            }
        }

        return directory;
    }

    /* (non-Javadoc)
     * @see org.mybatis.generator.api.ShellCallback#refreshProject(java.lang.String)
     */
    @Override
    public void refreshProject(String project) {
        // nothing to do in the default shell callback
    }

    /* (non-Javadoc)
     * @see org.mybatis.generator.api.ShellCallback#isMergeSupported()
     */
    @Override
    public boolean isMergeSupported() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.mybatis.generator.api.ShellCallback#isOverwriteEnabled()
     */
    @Override
    public boolean isOverwriteEnabled() {
        return overwrite;
    }

    /* (non-Javadoc)
     * @see org.mybatis.generator.api.ShellCallback#mergeJavaFile(java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
     */
//    @Override
    public String mergeJavaFile1(String newFileSource,
                                File existingFile, String[] javadocTags, String fileEncoding)
            throws ShellException {

        CompilationUnit newCompilationUnit = JavaParser.parse(newFileSource);
        CompilationUnit existingCompilationUnit = null;
        try {
            existingCompilationUnit = JavaParser.parse(existingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        StringBuilder sb = new StringBuilder(newCompilationUnit.getPackageDeclaration().get().toString());

        /**
         * 1. 合并imports
         */
        Set importSet = new HashSet<ImportDeclaration>();
        importSet.addAll(newCompilationUnit.getImports());
        importSet.addAll(existingCompilationUnit.getImports());

        for (Object i : importSet) {
            sb.append(i.toString());
        }
        newLine(sb);

        /**
         * 3. 合并注解
         */
        /**
         * 1. 合并imports
         */
        Set  Set = new HashSet<ImportDeclaration>();
        importSet.addAll(newCompilationUnit.getComments());
        importSet.addAll(existingCompilationUnit.getAllContainedComments());

        for (Object i : importSet) {
            sb.append(i.toString());
        }


        NodeList<TypeDeclaration<?>> types = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = existingCompilationUnit.getTypes();
        for (int i = 0; i < types.size(); i++) {
            //截取Class
            String classNameInfo = types.get(i).toString().substring(0, types.get(i).toString().indexOf("{") + 1);
            sb.append(classNameInfo);
            newLine(sb);
            newLine(sb);
            //合并fields
            List<FieldDeclaration> fields = types.get(i).getFields();
            List<FieldDeclaration> oldFields = oldTypes.get(i).getFields();
            HashSet<FieldDeclaration> fieldDeclarations = new HashSet<>();
            fieldDeclarations.addAll(fields);
            fieldDeclarations.addAll(oldFields);
            for (FieldDeclaration f : fieldDeclarations) {
                sb.append(f.toString());
                newLine(sb);
                newLine(sb);
            }

            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();
            List<MethodDeclaration> existingMethods = oldTypes.get(i).getMethods();
            for (MethodDeclaration f : methods) {
                sb.append(f.toString());
                newLine(sb);
                newLine(sb);
            }
            for (MethodDeclaration m : existingMethods) {
                boolean flag = true;
                for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
                    if (m.toString().contains(tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    sb.append(m.toString());
                    newLine(sb);
                    newLine(sb);
                }
            }

            //判断是否有内部类
//            types.get(i).getChildNodes();
//            for (Node n:types.get(i).getChildNodes()){
//                if (n.toString().contains("static class")){
//                    sb.append(n.toString());
//                }
//            }

        }

        return sb.append(System.getProperty("line.separator") + "}").toString();
    }

    @Override
    public String mergeJavaFile(
            String newFileSource, File existingFile, String[] javadocTags,
            String fileEncoding) throws ShellException {

        CompilationUnit newCompilationUnit = JavaParser.parse(newFileSource);
        CompilationUnit existingCompilationUnit = null;
        try {
            existingCompilationUnit = JavaParser.parse(existingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * 1. 合并import
         */
        Set importSet = new HashSet<ImportDeclaration>();
        importSet.addAll(newCompilationUnit.getImports());
        importSet.addAll(existingCompilationUnit.getImports());
        NodeList<ImportDeclaration> newImports = new NodeList<>();
        newImports.addAll(importSet);
        newCompilationUnit.setImports(newImports);

        /**
         * todo: 一个文件里面有多个类的情况没有考虑
         * 2. 合并成员变量和成员方法
         */
        TypeDeclaration<?> oldType = existingCompilationUnit.getType(0);
        TypeDeclaration<?> newType = newCompilationUnit.getType(0);

        NodeList members = oldType.getMembers();
        // 遍历所有tag
        for (String oldTag: javadocTags){
            // 遍历所有的字段
            // 删除所有带有特殊标记的字段
            members.removeIf(member -> ((BodyDeclaration) member).getComment().toString().contains(oldTag));
        }

        // 添加所有没有特殊标记的成员到 新文件
        newType.getMembers().addAll(members);

        /**
         * 3. 合并类的注解
         */
        Set<AnnotationExpr> anSet = new HashSet<>();
        anSet.addAll(oldType.getAnnotations());
        anSet.addAll(newType.getAnnotations());

        NodeList<AnnotationExpr> newAn = new NodeList<>();
        newAn.addAll(anSet);
        newType.setAnnotations(newAn);

        return newCompilationUnit.toString();
    }
}
