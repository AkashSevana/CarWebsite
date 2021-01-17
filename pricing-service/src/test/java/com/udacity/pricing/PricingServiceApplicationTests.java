package com.udacity.pricing;

import com.udacity.pricing.service.PriceException;
import com.udacity.pricing.service.PricingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PricingServiceApplicationTests {

	private Long vehicleId = 1L;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testPrice() throws PriceException {
		Assert.notNull(PricingService.getPrice(vehicleId), "Price retrieved!");
	}

}
