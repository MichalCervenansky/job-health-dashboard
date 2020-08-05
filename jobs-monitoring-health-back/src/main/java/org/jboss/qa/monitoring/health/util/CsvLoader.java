package org.jboss.qa.monitoring.health.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CsvLoader {

    public JSONArray getDataFromCSV(String csfFilePath) throws IOException, ParseException {

        if (csfFilePath.equals("")) {
            throw new FileNotFoundException();
        }

        JSONArray result;
        String outputFileName = "output.json";
        File output = new File(outputFileName);
        Object input = null;
        FileReader reader = null;

        try {
            input = getFileObjectFromJenkins(csfFilePath);

            CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
            CsvMapper csvMapper = new CsvMapper();

            List<Object> readAll = null;
            readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues((BufferedReader) input).readAll();

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);

            reader = new FileReader(output);
            result = getParsedDataArray(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
            Files.deleteIfExists(Paths.get(outputFileName));
        }
        return result;
    }

    private Reader getFileReaderFromJenkins(String csfFilePath) throws IOException {
        Reader input;
        URL urlFile = new URL(csfFilePath);

        HttpsURLConnection conn = (HttpsURLConnection) urlFile.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        input = new BufferedReader(in);

        return input;
    }

    private Object getFileObjectFromJenkins(String csfFilePath) throws IOException {
        Object input;
        URL urlFile = new URL(csfFilePath);

        HttpsURLConnection conn = (HttpsURLConnection) urlFile.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        input = new BufferedReader(in);

        return input;
    }

    public JSONArray getParsedDataArray(Reader reader) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray;

        Object obj = jsonParser.parse(reader);
        jsonArray = (JSONArray) obj;

        return jsonArray;
    }
}