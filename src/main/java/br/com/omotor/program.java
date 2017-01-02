package br.com.omotor;

import feign.*;
import feign.gson.GsonDecoder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


interface BCClient {

    @RequestLine("GET /dados/serie/bcdata.sgs.{serieId}/dados?formato=json")
    List<DataPoint> getSerieAllValues(@Param("serieId") int serieId);

    @RequestLine("GET /dados/serie/bcdata.sgs.{serieId}/dados?formato=json&dataInicial={startDate}&dataFinal={endDate}")
    List<DataPoint> getSerieRangeValues(@Param("serieId") int serieId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @RequestLine("GET /dados/serie/bcdata.sgs.{serieId}/dados/ultimos/{lastQty}?formato=json")
    List<DataPoint> getSerieLastValues(@Param("serieId") int serieId, @Param("lastQty") int lastQty);
}

class DataPoint {
    Date data;
    BigDecimal valor;
}

public class program {
    public static void main(String... args) {

        BCClient bcclient = Feign.builder().decoder(new GsonDecoder()).target(BCClient.class, "http://api.bcb.gov.br");

        List<DataPoint> result = bcclient.getSerieAllValues(1);
        System.out.println(result.size());

        result = bcclient.getSerieLastValues(1, 1);
        System.out.println(result.get(0).data);
        System.out.println(result.get(0).valor);
    }
}
