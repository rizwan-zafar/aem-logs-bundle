package com.aemlogs.services.Impl;
import org.osgi.service.metatype.annotations.*;
import com.aemlogs.services.AemLogsOsgiConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = AemLogsOsgiConfig.class,immediate = true)
@Designate(ocd = AemLogsOsgiConfigImpl.ServiceConfig.class )
public class AemLogsOsgiConfigImpl implements AemLogsOsgiConfig{

    @ObjectClassDefinition(name="AEM Sling Logs Configuration",
            description = "OSGi Configuration for AEM Sling logs")
    public @interface ServiceConfig {

        @AttributeDefinition(
                name = "Files Path",
                description = "Enter files Path Ex: '/Author/crx-quickstart/logs/'",
                type = AttributeType.STRING)
        public String filePath() default "";
    }

    private String filePath;

    @Activate
    protected void activate(ServiceConfig serviceConfig){
        filePath=serviceConfig.filePath();
    }

    @Override
    public String getFilePath() {
        return filePath;
    }
    
}
// only int int lines
// 
// /Users/rizwan.zafar/Documents/AEM STC/Instances/Author/crx-quickstart/logs/