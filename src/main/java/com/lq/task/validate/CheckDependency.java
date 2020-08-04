package com.lq.task.validate;

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

public class CheckDependency {

    public static final int REDIS_FLAG = 1;
    public static final int TORTOISE_FLAG = 2;

    private SpringBootCli springBootCli;

    public CheckDependency(SpringBootCli springBootCli) {
        this.springBootCli = springBootCli;
    }

    public void execute(int flag) throws Exception {
        boolean dependenciesNodeNonExist = true;
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        File pomFile = new File(springBootCli.getProjectPath() + File.separator + "pom.xml");
        Document document = db.parse(pomFile);
        Element project = (Element) document.getElementsByTagName("project").item(0);
        NodeList projectChildNodes = project.getChildNodes();
        Node dependenciesNode = null;
        for (int i = 0; i < projectChildNodes.getLength(); i++) {
            Node projectChild = projectChildNodes.item(i);
            if (projectChild.getNodeName().equals("dependencies")) {
                dependenciesNodeNonExist = false;
                dependenciesNode = projectChild;
            }
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
        if (!dependencies.contains("spring-boot-starter-data-redis") && (flag == REDIS_FLAG || flag == TORTOISE_FLAG)) {
            Element redis = createDependencyElm(document, "org.springframework.boot", "spring-boot-starter-data-redis");
            dependenciesNode.appendChild(redis);
            if (!dependencies.contains("jackson-databind")) {
                Element jackson = createDependencyElm(document, "com.fasterxml.jackson.core", "jackson-databind", "2.10.2");
                dependenciesNode.appendChild(jackson);
            }
            writeXml(document, pomFile);
            System.out.println("\n\n---------------------------------");
            System.err.println("redis config application.yml");
            String sb = "\n  redis: \n    host: " + "127.0.0.1" +
                    "\n    port: " + "6379" +
                    "\n    database: " + "0" +
                    "\n    timeout: " + "3000" +
                    "\n    jedis: \n      pool: " +
                    "\n        max-idle: " + "100" +
                    "\n        min-idle: " + "50" +
                    "\n        max-active: " + "150" +
                    "\n    password: " + "123456";
            System.out.println(sb);
            System.out.println("\n\n---------------------------------");
        }
        if (flag == TORTOISE_FLAG) {
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
            writeXml(document, pomFile);
        }
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
