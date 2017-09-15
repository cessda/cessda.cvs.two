package eu.cessda.cvmanager;

import org.gesis.security.util.Initialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CvGuiCommandLineRunner implements CommandLineRunner{
	
	@Autowired
	Initialization initialization;
        
    //this method will be executed just before SpringApplication.run(…​) is complete
    @Override
    public void run(String... arg0) throws Exception {
        initialization.init();
                        
        //YOUR CODE
    }
}