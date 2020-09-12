package org.spectrumauctions.sats.opt.model.mrvm;

import edu.harvard.econcs.jopt.solver.mip.MIP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.spectrumauctions.sats.core.model.mrvm.MRVMBidder;
import org.spectrumauctions.sats.core.model.mrvm.MRVMRegionsMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Created by Michael Weiss on 06.05.2017.
 */
public class Scalor {

    private static final Logger logger = LogManager.getLogger(Scalor.class);

    public static BigDecimal highestValidVal = BigDecimal.valueOf(MIP.MAX_VALUE - 1000000);


    public static double scalingFactor(Collection<MRVMBidder> bidders){
        double proposedSVScaling = calculateSVScalingFactor(bidders);
        double proposedOmegaScaling = calculateOmegaScalingFactor(bidders);
        if(proposedOmegaScaling > proposedSVScaling){
            return proposedOmegaScaling;
        }else{
            return proposedSVScaling;
        }
    }


    private static double calculateSVScalingFactor(Collection<MRVMBidder> bidders) {
        MRVMBidder biggestAlphaBidder = bidders.stream().max(Comparator.comparing(MRVMBidder::getAlpha)).orElseThrow(NoSuchElementException::new);
        MRVMRegionsMap.Region biggestRegion = bidders.stream().findAny().orElseThrow(NoSuchElementException::new).getWorld().getRegionsMap().getRegions().stream()
                .max(Comparator.comparing(MRVMRegionsMap.Region::getPopulation)).orElseThrow(NoSuchElementException::new);
        BigDecimal biggestAlpha = biggestAlphaBidder.getAlpha();
        BigDecimal biggestPopulation = BigDecimal.valueOf(biggestRegion.getPopulation());
        BigDecimal biggestC = bidders.stream().findAny().orElseThrow(NoSuchElementException::new).getWorld().getMaximumRegionalCapacity();
        BigDecimal securityBuffer = BigDecimal.valueOf(100000);
        BigDecimal biggestSv = biggestAlpha.multiply(biggestPopulation).multiply(biggestC).add(securityBuffer);
        BigDecimal MIP_MAX_VALUE = BigDecimal.valueOf(MIP.MAX_VALUE);
        BigDecimal proposedScalingFactor = biggestSv.divide(MIP_MAX_VALUE, RoundingMode.HALF_DOWN);
        if(proposedScalingFactor.compareTo(BigDecimal.ONE) <= 0){
            return 1.;
        }
        return Math.round(proposedScalingFactor.doubleValue())+1;
    }

    private static double calculateOmegaScalingFactor(Collection<MRVMBidder> bidders) {
        BigDecimal maxVal = biggestUnscaledPossibleValue(bidders);
        if (maxVal.compareTo(highestValidVal) < 0) {
            return 1;
        } else {
            logger.debug("Scaling MIP-CALC");
            BigDecimal proposedScalingFactor = maxVal.divide(highestValidVal, RoundingMode.HALF_DOWN);
            return Math.round(proposedScalingFactor.doubleValue())+1;
        }
    }

    /**
     * Returns the biggest possible value any of the passed bidders can have
     * @return
     */
    public static BigDecimal biggestUnscaledPossibleValue(Collection<MRVMBidder> bidders) {
        BigDecimal biggestValue = BigDecimal.ZERO;
        for (MRVMBidder bidder : bidders) {
            BigDecimal val = bidder.calculateValue(Bundle.of(bidder.getWorld().getLicenses()));
            if (val.compareTo(biggestValue) > 0) {
                biggestValue = val;
            }
        }
        return biggestValue;
    }
}
