package sagan;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import sagan.support.SetSystemProperty;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = IndexerApplication.class)
@Transactional
@ActiveProfiles(profiles = {SaganProfiles.STANDALONE})
public abstract class AbstractIndexerIntegrationTests {

	@ClassRule
	public static SetSystemProperty delay = new SetSystemProperty("search.indexer.delay", "60000000");

}
