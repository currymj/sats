/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.core.model.mrvm;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.spectrumauctions.sats.core.util.random.JavaUtilRNGSupplier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Michael Weiss
 *
 */
public class MRVMBidderTypeSpecificTest {

    private static MRVMWorld world;

    private static MRVMLocalBidder localBidder;
    private static MRVMRegionalBidder regionalBidder;
    private static MRVMNationalBidder globalBidder;

    private static Set<MRVMLicense> completeBundle;

    @BeforeClass
    public static void beforeClass() {
        world = new MRVMWorld(MRMSimpleWorldGen.getSimpleWorldBuilder(), new JavaUtilRNGSupplier(234872L));
        localBidder = (MRVMLocalBidder) world.createPopulation(MRMSimpleWorldGen.getSimpleLocalBidderSetup(), null, null, new JavaUtilRNGSupplier(234873L)).iterator().next();
        regionalBidder = (MRVMRegionalBidder) world.createPopulation(null, MRMSimpleWorldGen.getSimpleRegionalBidderSetup(), null, new JavaUtilRNGSupplier(3984274L)).iterator().next();
        globalBidder = (MRVMNationalBidder) world.createPopulation(null, null, MRMSimpleWorldGen.getSimpleGlobalBidderSetup(), new JavaUtilRNGSupplier(29842793L)).iterator().next();
        completeBundle = new HashSet<>(world.getLicenses());

    }

    @Test
    public void localGammaValuesTestOnCompleteBundle() {
        //Gamma Values if bidder has complete bundle (note that bundle should not have an influence)
        Map<MRVMRegionsMap.Region, BigDecimal> gammaValues = localBidder.gammaFactors(completeBundle);
        int interestRegionsCount = 0;
        for (Entry<MRVMRegionsMap.Region, BigDecimal> gammaEntry : gammaValues.entrySet()) {
            if (localBidder.regionsOfInterest.contains(gammaEntry.getKey().getId())) {
                interestRegionsCount++;
                Assert.assertEquals(0, BigDecimal.ONE.compareTo(gammaEntry.getValue()));
            }
        }
        Assert.assertEquals(3, interestRegionsCount);
    }

    /**
     * Note: Validity of distance calculation is tested in {@link MRVMWorldTest#regionalDistanceTests()}
     */
    @Test
    public void regionalGammaValuesTest() {
        //First constraint: All regional gammas have to be in (0,1] and not dependent on bundle
        Map<MRVMRegionsMap.Region, BigDecimal> calculatedGammas = regionalBidder.gammaFactors(null);
        for (Entry<MRVMRegionsMap.Region, BigDecimal> regionEntry : calculatedGammas.entrySet()) {
            if (regionEntry.getKey().equals(regionalBidder.home)) {
                Assert.assertEquals(0, regionEntry.getValue().compareTo(BigDecimal.ONE));
            } else {
                Assert.assertTrue(regionEntry.getValue().compareTo(BigDecimal.ONE) < 0);
                Assert.assertTrue(regionEntry.getValue().compareTo(BigDecimal.ZERO) > 0);
            }
        }
        //TODO actual calculation on any region
    }

    @Test
    public void globalGammaValuesTestOnCompleteBundle() {
        //Gamma Value for the complete bundle should be 1, i.e., there is no discount if global bidder has all licenses
        BigDecimal returnedGammaValue = globalBidder.gammaFactor(null, completeBundle);
        Assert.assertEquals(0, BigDecimal.ONE.compareTo(returnedGammaValue));
        for (BigDecimal gammaFromMap : globalBidder.gammaFactors(completeBundle).values()) {
            Assert.assertEquals(0, BigDecimal.ONE.compareTo(gammaFromMap));
        }
    }

    @Test
    public void emptyBundleTest() {
        Assert.assertEquals(BigDecimal.ZERO, localBidder.calculateValue(Bundle.EMPTY));
        Assert.assertEquals(BigDecimal.ZERO, globalBidder.calculateValue(Bundle.EMPTY));
        Assert.assertEquals(BigDecimal.ZERO, regionalBidder.calculateValue(Bundle.EMPTY));
    }


}
