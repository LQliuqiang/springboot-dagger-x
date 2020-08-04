package com.lq.task;

import com.lq.SpringBootCli;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class CreatePomXmlTask {

    private SpringBootCli springBootCli;

    public CreatePomXmlTask(SpringBootCli springBootCli) {
        this.springBootCli = springBootCli;
    }

    public void execute() throws Exception {
        boolean parentNodeNonExist = true, propertiesNodeNonExist = true, dependenciesNodeNonExist = true, buildNodeNonExist = true;
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        File pomFile = new File(springBootCli.getProjectPath() + File.separator + "pom.xml");
        Document document = db.parse(pomFile);
        Element project = (Element) document.getElementsByTagName("project").item(0);
        NodeList projectChildNodes = project.getChildNodes();
        Node dependenciesNode = null;
        for (int i = 0; i < projectChildNodes.getLength(); i++) {
            Node projectChild = projectChildNodes.item(i);
            if (projectChild.getNodeName().equals("parent")) {
                parentNodeNonExist = false;
            }
            if (projectChild.getNodeName().equals("properties")) {
                propertiesNodeNonExist = false;
            }
            if (projectChild.getNodeName().equals("dependencies")) {
                dependenciesNodeNonExist = false;
                dependenciesNode = projectChild;
            }
            if (projectChild.getNodeName().equals("build")) {
                buildNodeNonExist = false;
            }
        }
        if (parentNodeNonExist) {
            Element parentElm = document.createElement("parent");
            Element groupIdElm = document.createElement("groupId");
            groupIdElm.setTextContent("org.springframework.boot");
            Element artifactIdElm = document.createElement("artifactId");
            artifactIdElm.setTextContent("spring-boot-starter-parent");
            Element versionElm = document.createElement("version");
            versionElm.setTextContent(springBootCli.getSpringBootVersion());
            Element relativePathElm = document.createElement("relativePath");
            parentElm.appendChild(groupIdElm);
            parentElm.appendChild(artifactIdElm);
            parentElm.appendChild(versionElm);
            parentElm.appendChild(relativePathElm);
            project.appendChild(parentElm);
        }
        if (propertiesNodeNonExist) {
            Element packagingElm = document.createElement("properties");
            Element jdk8Eml = document.createElement("java.version");
            jdk8Eml.setTextContent("1.8");
            packagingElm.appendChild(jdk8Eml);
            project.appendChild(packagingElm);
        }
        if (dependenciesNodeNonExist) {
            dependenciesNode = document.createElement("dependencies");
        }
        List<String> dependencies = new ArrayList<>();
        NodeList dependenciesChildNodes = dependenciesNode.getChildNodes();
        for (int i = 0; i < dependenciesChildNodes.getLength(); i++) {
            Node node = dependenciesChildNodes.item(i);
            if (!node.getNodeName().equals("#text")) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node node1 = childNodes.item(y);
                    if (!node1.getNodeName().equals("#text") && node1.getNodeName().equals("artifactId")) {
                        dependencies.add(node1.getTextContent());
                    }
                }
            }
        }
        if (!dependencies.contains("spring-boot-starter")) {
            Element springBootStarter = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter");
            dependenciesNode.appendChild(springBootStarter);
        }
        if (!dependencies.contains("spring-boot-starter-web")) {
            Element springBootWeb = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter-web");
            dependenciesNode.appendChild(springBootWeb);
        }
        if (!dependencies.contains("spring-boot-starter-actuator")) {
            Element springBootWeb = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter-actuator");
            dependenciesNode.appendChild(springBootWeb);
        }
        if (!dependencies.contains("spring-boot-configuration-processor")) {
            Element dependencyElm = document.createElement("dependency");
            Element groupIdElm = document.createElement("groupId");
            groupIdElm.setTextContent("org.springframework.boot");
            Element artifactIdElm = document.createElement("artifactId");
            artifactIdElm.setTextContent("spring-boot-configuration-processor");
            Element optionalElm = document.createElement("optional");
            optionalElm.setTextContent("true");
            dependencyElm.appendChild(groupIdElm);
            dependencyElm.appendChild(artifactIdElm);
            dependencyElm.appendChild(optionalElm);
            dependenciesNode.appendChild(dependencyElm);
        }
        if (!dependencies.contains("fastjson")) {
            Element fastjson = createDependencyElm(document, "com.alibaba", "fastjson", springBootCli.getFastJsonVersion());
            dependenciesNode.appendChild(fastjson);
        }
        if (!dependencies.contains("mybatis-spring-boot-starter")) {
            Element mybatis = createDependencyElm(document, "org.mybatis.spring.boot", "mybatis-spring-boot-starter", springBootCli.getMybatisVersion());
            dependenciesNode.appendChild(mybatis);
        }
        if (!dependencies.contains("mysql-connector-java")) {
            Element mysql = createDependencyElm(document, "mysql", "mysql-connector-java", springBootCli.getMysqlConnectorVersion());
            dependenciesNode.appendChild(mysql);
        }
        if (!dependencies.contains("druid")) {
            Element druid = createDependencyElm(document, "com.alibaba", "druid", springBootCli.getDruidVersion());
            dependenciesNode.appendChild(druid);
        }
        if (!dependencies.contains("spring-boot-starter-data-redis") && springBootCli.isUseRedis()) {
            Element redis = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter-data-redis");
            dependenciesNode.appendChild(redis);
            if (!dependencies.contains("jackson-databind")) {
                Element jackson = createDependencyElm(document, "com.fasterxml.jackson.core", "jackson-databind", "2.10.2");
                dependenciesNode.appendChild(jackson);
            }
        }
        if (!dependencies.contains("spring-boot-starter-log4j2")) {
            Element log4j2 = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter-log4j2");
            dependenciesNode.appendChild(log4j2);
            Element elm = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter");
            Element exclusions = document.createElement("exclusions");
            Element exclusion = document.createElement("exclusion");
            Element groupId = document.createElement("groupId");
            Element artifactId = document.createElement("artifactId");
            groupId.setTextContent("org.springframework.boot");
            artifactId.setTextContent("spring-boot-starter-logging");
            exclusion.appendChild(artifactId);
            exclusions.appendChild(exclusion).appendChild(groupId);
            elm.appendChild(exclusions);
            dependenciesNode.appendChild(elm);
        }
        project.appendChild(dependenciesNode);
        if (buildNodeNonExist) {
            Element build = document.createElement("build");
            Element plugins = document.createElement("plugins");
            Element plugin = document.createElement("plugin");
            Element groupId = document.createElement("groupId");
            Element artifactId = document.createElement("artifactId");
            groupId.setTextContent("org.springframework.boot");
            artifactId.setTextContent("spring-boot-maven-plugin");
            plugin.appendChild(artifactId);
            build.appendChild(plugins).appendChild(plugin).appendChild(groupId);
            project.appendChild(build);
        }

        writeXml(document, pomFile);
    }


    private Element createDependencyElm(Document document, String groupId, String artifactId) {
        return createDependencyElm(document, groupId, artifactId, null);
    }

    private Element createDependencyElm(Document document, String groupId, String artifactId, String version) {
        Element dependencyElm = document.createElement("dependency");
        Element groupIdElm = document.createElement("groupId");
        groupIdElm.setTextContent(groupId);
        Element artifactIdElm = document.createElement("artifactId");
        artifactIdElm.setTextContent(artifactId);
        dependencyElm.appendChild(groupIdElm);
        dependencyElm.appendChild(artifactIdElm);
        if (version != null) {
            Element versionElm = document.createElement("version");
            versionElm.setTextContent(version);
            dependencyElm.appendChild(versionElm);
        }
        return dependencyElm;
    }

    private void writeXml(Document document, File pomFile) throws TransformerException {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(pomFile);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, result);
    }

}
