package eu.cessda.cvs.web.rest;

import eu.cessda.cvs.service.dto.VersionDTO;
import eu.cessda.cvs.utils.VersionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ResourceUtils {

    private ResourceUtils(){}

    static ResponseEntity<List<String>> getListResponseEntity(VersionDTO version1, VersionDTO version2) {
        List<String> compareVersions = VersionUtils.buildComparisonCurrentAndPreviousCV(version1, version2);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Prev-Cv-Version", version2.getNotation() + " " +version2.getItemType() + " v." + version2.getNumber());
        headers.add("X-Current-Cv-Version", version1.getNotation() + " " +version1.getItemType() + " v." + version1.getNumber());
        return ResponseEntity.ok().headers(headers).body(compareVersions);
    }

    public static String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        return basePath;
    }
}
