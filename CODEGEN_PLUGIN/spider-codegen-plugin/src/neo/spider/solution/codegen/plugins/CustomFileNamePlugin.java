package neo.spider.solution.codegen.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import neo.spider.solution.codegen.util.StringUtil;

import java.util.List;

/*
 * TableConfiguration에 domain 이름을 table명DTO로 설정해두었기 때문에
 * mapper 인터페이스와 xml의 이름도 table명DTOMapper로 생성됨
 * mapper 인터페이스와 xml의 이름을 table명Mapper로 다시 설정해주는 플러그인
 * 
 * initialized 메소드에서 TableConfiguration 객체에 접근하여 DTO 명을 수정하려고 하였으나
 * 반영되지 않아서 MyBatisGeneratorProgrammatic에서 DTO 명을 세팅한 후
 * mapper의 이름을 변경하는 방식으로 구현
 */
public class CustomFileNamePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true; // 플러그인 유효성 검증
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 원래 테이블 이름 가져오기
        String originalTableName = StringUtil.toPascalCase(introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());

        // Mapper 및 XML 이름은 기존 이름 유지
        String mapperClassName = originalTableName + "Mapper";
        introspectedTable.setMyBatis3JavaMapperType(introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetPackage() + "." + mapperClassName);
        introspectedTable.setMyBatis3XmlMapperFileName(mapperClassName + ".xml");
    }
    
}
