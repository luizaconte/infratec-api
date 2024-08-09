package br.com.infratec.service.sales;

import br.com.infratec.client.ViaCepClient;
import br.com.infratec.config.Constants;
import br.com.infratec.dto.ConfiguracaoDTO;
import br.com.infratec.dto.meli.*;
import br.com.infratec.dto.viacep.ConsultaCepDTO;
import br.com.infratec.enums.AmbienteNfe;
import br.com.infratec.enums.EstadosBrasil;
import br.com.infratec.enums.TipoPessoa;
import br.com.infratec.enums.meli.TipoDocumento;
import br.com.infratec.enums.meli.TipoInfo;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.erp.Produto;
import br.com.infratec.model.sales.TbConfigNcm;
import br.com.infratec.model.sales.TbMunicipio;
import br.com.infratec.model.sales.TbNcm;
import br.com.infratec.nfe.enums.DocumentoEnum;
import br.com.infratec.nfe.enums.EstadosEnum;
import br.com.infratec.nfe.schema_4.enviNFe.*;
import br.com.infratec.nfe.util.ChaveUtil;
import br.com.infratec.nfe.util.ConstantesUtil;
import br.com.infratec.service.erp.ProdutoService;
import br.com.infratec.util.XmlSignator;
import br.com.infratec.util.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NfeService {

    private static final double pisValue = 1.65;
    private static final double cofinsValue = 7.6;
    private static final double icmsValue = 18.0;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'-03:00'");

    private static final String INFPROT_TEMPLATE = "<protNFe versao=\"4.00\">\n" +
            "	<infProt Id=\"%s\">\n" +
            "		<tpAmb>1</tpAmb>\n" +
            "		<verAplic>SP_NFE_PL009_V4</verAplic>\n" +
            "		<chNFe>%s</chNFe>\n" +
            "		<dhRecbto>%s</dhRecbto>\n" +
            "		<nProt>%s</nProt>\n" +
            "		<digVal>%s</digVal>\n" +
            "		<cStat>100</cStat>\n" +
            "       <xMotivo>Autorizado o uso da NF-e</xMotivo>\n" +
            "	</infProt>\n" +
            "</protNFe>";

    private final ObjectFactory objectFactory;
    private final MunicipioService municipioService;
    private final ProdutoService produtoService;
    private final NcmService ncmService;
    private final ConfigNcmService configNcmService;
    private final ViaCepClient viaCepClient;

    public NfeService(MunicipioService municipioService, ProdutoService produtoService,
                      NcmService ncmService, ConfigNcmService configNcmService, ViaCepClient viaCepClient) {
        this.municipioService = municipioService;
        this.produtoService = produtoService;
        this.ncmService = ncmService;
        this.configNcmService = configNcmService;
        this.viaCepClient = viaCepClient;
        this.objectFactory = new ObjectFactory();
    }

    public String montarXML(Order order, Billing billing, Shipment shipment, List<Product> productList, Long numNfe) {
        try {

            NfeProc nfeProc = montarEnviNfe(order, billing, shipment, productList, numNfe);

            String xmlAssinado = sign(nfeProc, NfeProc.class, "infNFe");

            xmlAssinado = xmlAssinado.replace("nfeProc xmlns:ns2=\"http://www.portalfiscal.inf.br/nfe\" xmlns:ns3=\"http://www.w3.org/2000/09/xmldsig#\" versao=\"4.00\"", "nfeProc versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\"");

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xmlAssinado));

            Document doc = builder.parse(src);
            String disgestValue = doc.getElementsByTagName("DigestValue").item(0).getTextContent();

            String infProt = getInfProt(nfeProc.getNFe().get(0).getInfNFe().getId().replace("NFe", ""), disgestValue);

            StringBuilder xml = new StringBuilder(xmlAssinado);

            xml.insert(xmlAssinado.indexOf("</NFe>") + 6, infProt);

            return xml.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getInfProt(String chave, String digestValue) {
        long nroProtocolo = (long) (Math.random() * 100000000000000L);
        String dataRecebimento = DATE_TIME_FORMATTER.format(OffsetDateTime.now());
        return String.format(INFPROT_TEMPLATE, "Id" + nroProtocolo, chave, dataRecebimento, nroProtocolo, digestValue);
    }

    private String sign(final Object obj, final Class<?> type, final String... tags) {
        try {
            final var xmlLiteral = XmlUtils.xmlToString(obj, type);
            final var xmlSignator = XmlSignator.newInstance(getPrivateKeyEntry());
            return xmlSignator.sign(xmlLiteral, tags);
        } catch (final Exception e) {
            throw new RuntimeException("Cannot sign the xml", e);
        }
    }

    private KeyStore.PrivateKeyEntry getPrivateKeyEntry() {
        try {
            final var password = Constants.APP_CONFIG.getSenha().toCharArray();
            final var keyStore = getKeyStore(password);
            final String alias = keyStore.aliases().nextElement();
            final var passwordProtection = new KeyStore.PasswordProtection(password);
            return (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, passwordProtection);
        } catch (final Exception e) {
            throw new RuntimeException("Cannot get PrivateKeyEntry", e);
        }
    }

    private KeyStore getKeyStore(final char[] password) {
        try {
            final var pkcs12 = KeyStore.getInstance("PKCS12");
            pkcs12.load(new ByteArrayInputStream(Constants.APP_CONFIG.getCertificado()), password);
            return pkcs12;
        } catch (final Exception e) {
            throw new RuntimeException("Cannot load the KeyStore", e);
        }
    }

    public NfeProc montarEnviNfe(Order order, Billing billing, Shipment shipment, List<Product> productList, Long numeroNfe) {
        try {
            LocalDateTime dataEmissao = LocalDateTime.now();
            String cnf = ChaveUtil.completarComZerosAEsquerda(String.valueOf(numeroNfe), 8);
            String modelo = DocumentoEnum.NFE.getModelo();
            int serie = 1;
            //Informe o tipo de Emissao da NFe TODO
            String tipoEmissao = "1";

            EstadosEnum uf = Arrays.stream(EstadosEnum.values()).filter(ufEnum -> ufEnum.name().equals(Constants.APP_CONFIG.getUf())).findFirst().orElse(EstadosEnum.SP);

            // MontaChave a NFe
            ChaveUtil chaveUtil = new ChaveUtil(EstadosEnum.SP, Constants.APP_CONFIG.getCnpj(), modelo, serie, numeroNfe, tipoEmissao, cnf, dataEmissao);
            String chave = chaveUtil.getChaveNF();
            String cdv = chaveUtil.getDigitoVerificador();

            TNFe.InfNFe infNFe = objectFactory.createTNFeInfNFe();
            infNFe.setId(chave);
            infNFe.setVersao(ConstantesUtil.VERSAO.NFE);

            //Preenche IDE
            infNFe.setIde(preencheIde(cnf, numeroNfe, tipoEmissao, modelo, serie, cdv, uf));

            //Preenche Emitente- Tubarão
            infNFe.setEmit(preencheEmitente(uf));

            //Preenche o Destinatario
            infNFe.setDest(preencheDestinatario(billing.getBillingInfo()));

            infNFe.setEntrega(preencheEnderecoEntrega(shipment, billing.getBillingInfo().getDocType(), billing.getBillingInfo().getDocNumber()));

            List<Produto> produtos = new ArrayList<>();
            for (Product product : productList) {
                produtoService.findById(Long.valueOf(product.getSku())).ifPresent(produtos::add);
            }

            //Preenche os dados do Produto da Nfe e adiciona a Lista
            infNFe.getDet().addAll(preencheDet(order.getId(), order.getOrderItems(), productList, produtos, billing.getBillingInfo().getInfo(TipoInfo.STATE_NAME), billing.getBillingInfo().getDocType().equals(TipoDocumento.CPF)));

            //Preenche totais da NFe
            infNFe.setTotal(preencheTotal(order));

            //Preenche os dados de Transporte
            infNFe.setTransp(preencheTransporte(produtos));

            // Preenche dados Pagamento
            infNFe.setPag(preenchePagamento(order.getPaidAmount(), order.getTotalAmount()));

            TNFe nfe = objectFactory.createTNFe();
            nfe.setInfNFe(infNFe);

            // Monta EnviNfe
            NfeProc enviNFe = objectFactory.createNfeProc();
            enviNFe.setVersao(ConstantesUtil.VERSAO.NFE);
            enviNFe.setIdLote(null);
            //enviNFe.setIndSinc("1");
            enviNFe.getNFe().add(nfe);

            return enviNFe;
        } catch (Exception e) {
            e.getCause();
            throw new ZCException(e);
        }
    }

    /**
     * Preenche o IDE
     */
    private TNFe.InfNFe.Ide preencheIde(String cnf, Long numeroNfe, String tipoEmissao, String modelo, int serie, String cDv, EstadosEnum uf) {
        TNFe.InfNFe.Ide ide = objectFactory.createTNFeInfNFeIde();
        ide.setCUF(uf.getCodigoUF());
        ide.setCNF(cnf);
        ide.setNatOp("VENDA MERCADORIA AD OU RECEB TERCEIROS");
        ide.setMod(modelo);
        ide.setSerie(String.valueOf(serie));

        String data = DATE_TIME_FORMATTER.format(OffsetDateTime.now());
        ide.setDhEmi(data);
        ide.setDhSaiEnt(data);

        ide.setNNF(String.valueOf(numeroNfe));
        ide.setTpNF("1");
        ide.setIdDest("1");
        ide.setCMunFG(Constants.APP_CONFIG.getCodMunicipio());
        ide.setTpImp("1");
        ide.setTpEmis(tipoEmissao);
        ide.setCDV(cDv);
        ide.setTpAmb(AmbienteNfe.PRODUCAO.getCodigo());
        ide.setFinNFe("1");
        ide.setIndFinal("1");
        ide.setIndPres("1");
        ide.setProcEmi("0");
        ide.setVerProc("Master v3.0 - NF-e");

        return ide;
    }

    /**
     * Preenche o Emitente da Nfe- Tubarão
     */
    private TNFe.InfNFe.Emit preencheEmitente(EstadosEnum uf) {
        ConfiguracaoDTO config = Constants.APP_CONFIG;
        TNFe.InfNFe.Emit emit = objectFactory.createTNFeInfNFeEmit();
        emit.setCNPJ(config.getCnpj());
        emit.setXNome(config.getNome());
        emit.setXFant(config.getNomeFantasia());

        TEnderEmi enderEmit = objectFactory.createTEnderEmi();
        enderEmit.setXLgr(config.getLogradouro());
        enderEmit.setNro(config.getNumero());
        enderEmit.setXCpl(config.getCpl());
        enderEmit.setXBairro(config.getBairro());
        enderEmit.setCMun(config.getCodMunicipio());
        enderEmit.setXMun(StringUtils.upperCase(config.getNomeMunicipio()));
        enderEmit.setUF(TUfEmi.valueOf(uf.toString()));
        enderEmit.setCEP(config.getCep());
        enderEmit.setCPais("1058");
        enderEmit.setXPais("BRASIL");
        enderEmit.setFone(config.getTelefone());
        emit.setEnderEmit(enderEmit);
        emit.setIE(config.getIe());
        // emit.setIEST(); TODO- preencher ?
        emit.setCRT(config.getCrt());
        return emit;
    }

    /**
     * Preenche o Destinatario da NFe
     */
    private TNFe.InfNFe.Dest preencheDestinatario(BillingInfo billingInfo) {
        TNFe.InfNFe.Dest dest = objectFactory.createTNFeInfNFeDest();
        if (billingInfo.getDocType().equals(TipoDocumento.CPF)) {
            dest.setCPF(billingInfo.getDocNumber());
            dest.setXNome(billingInfo.getInfo(TipoInfo.FIRST_NAME) + " " + billingInfo.getInfo(TipoInfo.LAST_NAME));
        } else {
            dest.setCNPJ(billingInfo.getDocNumber());
            dest.setXNome(billingInfo.getInfo(TipoInfo.BUSINESS_NAME));
        }
        TEndereco enderDest = objectFactory.createTEndereco();
        enderDest.setXLgr(billingInfo.getInfo(TipoInfo.STREET_NAME));
        enderDest.setNro(billingInfo.getInfo(TipoInfo.STREET_NUMBER));
        enderDest.setXBairro(billingInfo.getInfo(TipoInfo.NEIGHBORHOOD));
        enderDest.setCEP(billingInfo.getInfo(TipoInfo.ZIP_CODE));
        enderDest.setCPais(billingInfo.getInfo(TipoInfo.COUNTRY_ID));
        enderDest.setXPais("BRASIL");
        enderDest.setXCpl(billingInfo.getInfo(TipoInfo.COMMENT));
        dest.setEnderDest(enderDest);
        dest.setIndIEDest("9");

        //enderDest.setFone("4845454545");
        //dest.setEmail("teste@test");

        Optional<TbMunicipio> tbMunicipio = municipioService.buscarMunicipio(billingInfo.getInfo(TipoInfo.CITY_NAME), billingInfo.getInfo(TipoInfo.STATE_NAME));
        if (tbMunicipio.isPresent()) {
            enderDest.setCMun(tbMunicipio.get().getCodigo());
            enderDest.setXMun(tbMunicipio.get().getNome());
            enderDest.setUF(TUf.valueOf(tbMunicipio.get().getUf()));
        } else {
            ConsultaCepDTO cepDTO = viaCepClient.buscaCEP(billingInfo.getInfo(TipoInfo.ZIP_CODE)).getBody();
            if (Objects.nonNull(cepDTO)) {
                enderDest.setCMun(cepDTO.getIbge());
                enderDest.setXMun(cepDTO.getLocalidade());
                enderDest.setUF(TUf.valueOf(cepDTO.getUf()));
            }
        }
        return dest;
    }

    /**
     * Preenche o Destinatario da NFe
     */
    private TLocal preencheEnderecoEntrega(Shipment shipment, TipoDocumento tipoDocumento, String documento) {
        TLocal tLocal = objectFactory.createTLocal();
        tLocal.setXNome(StringUtils.upperCase(shipment.getReceiverAddress().getReceiverName()));
        if (tipoDocumento.equals(TipoDocumento.CPF)) {
            tLocal.setCPF(documento);
        } else {
            tLocal.setCNPJ(documento);
        }

        tLocal.setCEP(shipment.getReceiverAddress().getZipCode());
        tLocal.setXMun(StringUtils.upperCase(shipment.getReceiverAddress().getCity().getName()));
        Optional<TbMunicipio> tbMunicipio = municipioService.buscarMunicipio(StringUtils.upperCase(shipment.getReceiverAddress().getCity().getName()), shipment.getReceiverAddress().getState().getName());
        if (tbMunicipio.isPresent()) {
            tLocal.setCMun(tbMunicipio.get().getCodigo());
            tLocal.setUF(TUf.valueOf(EstadosBrasil.of(shipment.getReceiverAddress().getState().getName())));
        } else {
            ConsultaCepDTO cepDTO = viaCepClient.buscaCEP(shipment.getReceiverAddress().getZipCode()).getBody();
            if (Objects.nonNull(cepDTO)) {
                tLocal.setCMun(cepDTO.getIbge());
                tLocal.setXMun(cepDTO.getLocalidade());
                tLocal.setUF(TUf.valueOf(cepDTO.getUf()));
            }
        }
        tLocal.setXPais(StringUtils.upperCase(shipment.getReceiverAddress().getCountry().getName()));
        tLocal.setCPais("1058");
        tLocal.setXLgr(shipment.getReceiverAddress().getStreetName());
        tLocal.setNro(shipment.getReceiverAddress().getStreetNumber());
        tLocal.setXCpl(StringUtils.truncate(shipment.getReceiverAddress().getComment(), 60));
        tLocal.setXBairro(shipment.getReceiverAddress().getNeighborhood().getName());
        return tLocal;
    }


    /**
     * Preenche Det Nfe
     */
    private List<TNFe.InfNFe.Det> preencheDet(Long orderID, List<OrderItem> orderItems, List<Product> productList, List<Produto> produtos, String uf, boolean isFisica) {
        List<TNFe.InfNFe.Det> detList = new ArrayList<>();
        String ufCliente = EstadosBrasil.of(uf);
        TipoPessoa tipoPessoa = isFisica ? TipoPessoa.FISICA : TipoPessoa.JURIDICA;

        //O Preenchimento deve ser feito por produto, Então deve ocorrer uma LIsta
        orderItems.forEach(item -> {
            TNFe.InfNFe.Det det = objectFactory.createTNFeInfNFeDet();
            //O numero do Item deve seguir uma sequencia
            det.setNItem(String.valueOf(item.getElementId() + 1));
            Optional<Product> productItem = productList.stream().filter(p -> p.getSku().equals(item.getItem().getSellerSku())).findFirst();
            Optional<Produto> produto = produtos.stream().filter(p -> p.getProCodigo().toString().equals(item.getItem().getSellerSku())).findFirst();
            // Preenche dados do Produto
            det.setProd(preencheProduto(orderID, item, productItem, produto, ufCliente, tipoPessoa));
            det.setImposto(preencheImposto(item));

            detList.add(det);
        });
        return detList;
    }

    /**
     * Preenche dados do Produto
     */
    private TNFe.InfNFe.Det.Prod preencheProduto(Long orderID, OrderItem item, Optional<Product> product, Optional<Produto> produto, String uf, TipoPessoa tipoPessoa) {
        TNFe.InfNFe.Det.Prod prod = objectFactory.createTNFeInfNFeDetProd();
        prod.setCProd(item.getItem().getSellerSku());
        prod.setXProd(item.getItem().getTitle());
        prod.setUCom(item.getRequestedQuantity().getMeasure());
        prod.setQCom(String.valueOf(item.getQuantity()));
        prod.setVUnCom(String.valueOf(item.getUnitPrice()));
        prod.setVProd(String.valueOf(item.getQuantity() * item.getFullUnitPrice()));
        prod.setUTrib("UNI");
        prod.setQTrib(String.valueOf(item.getQuantity()));
        prod.setVUnTrib(String.valueOf(item.getUnitPrice()));
        prod.setIndTot(String.valueOf(item.getQuantity()));
        prod.setXPed(orderID.toString());
        prod.setCFOP("5102");
        prod.setVDesc("0.0");

        if (product.isPresent()) {
            if (Objects.nonNull(product.get().getTaxInformation()) && Objects.nonNull(product.get().getTaxInformation().getNcm())) {
                prod.setNCM(product.get().getTaxInformation().getNcm());
                prod.setCEST(product.get().getTaxInformation().getCest());
            } else {
                if (produto.isPresent()) {
                    prod.setNCM(produto.get().getProNcm());
                } else {
                    prod.setNCM("00000000");
                }
            }

            if (Objects.nonNull(produto.get().getProNcm()) && !produto.get().getProNcm().isBlank()) {
                try {
                    Optional<TbNcm> tbNcm = ncmService.findByNumero(produto.get().getProNcm());
                    if (tbNcm.isPresent()) {
                        Optional<TbConfigNcm> configNcm = configNcmService.buscarNcm(tbNcm.get().getId(), tipoPessoa, uf);
                        configNcm.ifPresent(tbConfigNcm -> prod.setVDesc(String.valueOf(tbConfigNcm.getValorDesconto())));
                    }
                } catch (Exception ignored) {
                }
            }
        }


        return prod;
    }

    /**
     * Preenche dados do Imposto da Nfe
     */
    private TNFe.InfNFe.Det.Imposto preencheImposto(OrderItem orderItem) {
        TNFe.InfNFe.Det.Imposto imposto = objectFactory.createTNFeInfNFeDetImposto();

        TNFe.InfNFe.Det.Imposto.ICMS icms = objectFactory.createTNFeInfNFeDetImpostoICMS();

        // TODO- ver tag icms
        TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = objectFactory.createTNFeInfNFeDetImpostoICMSICMS00();
        icms00.setOrig("0");
        icms00.setCST("00");
        icms00.setModBC("0");
        icms00.setVBC(String.valueOf(orderItem.getFullUnitPrice()));
        icms00.setPICMS(String.valueOf(icmsValue));
        icms00.setVICMS(String.valueOf(arredondar(orderItem.getFullUnitPrice() * (icmsValue / 100))));

        icms.setICMS00(icms00);

        TNFe.InfNFe.Det.Imposto.PIS pis = objectFactory.createTNFeInfNFeDetImpostoPIS();
        TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = objectFactory.createTNFeInfNFeDetImpostoPISPISAliq();
        pisAliq.setCST("01");
        pisAliq.setVBC(String.valueOf(orderItem.getFullUnitPrice()));
        pisAliq.setPPIS(String.valueOf(pisValue));
        pisAliq.setVPIS(String.valueOf(arredondar(orderItem.getFullUnitPrice() * (pisValue / 100))));
        pis.setPISAliq(pisAliq);

        TNFe.InfNFe.Det.Imposto.COFINS cofins = objectFactory.createTNFeInfNFeDetImpostoCOFINS();
        TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = objectFactory.createTNFeInfNFeDetImpostoCOFINSCOFINSAliq();
        cofinsAliq.setCST("01");
        cofinsAliq.setVBC(String.valueOf(orderItem.getFullUnitPrice()));
        cofinsAliq.setPCOFINS(String.valueOf(cofinsValue));
        cofinsAliq.setVCOFINS(String.valueOf(arredondar(orderItem.getFullUnitPrice() * (cofinsValue / 100))));
        cofins.setCOFINSAliq(cofinsAliq);


        TNFe.InfNFe.Det.Imposto.ICMSUFDest icmsufDest = objectFactory.createTNFeInfNFeDetImpostoICMSUFDest();
        icmsufDest.setVBCUFDest(String.valueOf(orderItem.getFullUnitPrice()));
        icmsufDest.setVBCFCPUFDest(String.valueOf(orderItem.getFullUnitPrice()));
//        icmsufDest.setPFCPUFDest(); TODO- como preencher
//        icmsufDest.setPICMSUFDest();
//        icmsufDest.setPICMSInter();
//        icmsufDest.setPICMSInterPart();
//        icmsufDest.setVFCPUFDest();
//        icmsufDest.setVICMSUFDest();
//        icmsufDest.setVICMSUFRemet();

        // imposto.getContent().add(objectFactory.createTNFeInfNFeDetImpostoVTotTrib()); TODO- como preencher
        imposto.getContent().add(objectFactory.createTNFeInfNFeDetImpostoICMS(icms));
        imposto.getContent().add(objectFactory.createTNFeInfNFeDetImpostoPIS(pis));
        imposto.getContent().add(objectFactory.createTNFeInfNFeDetImpostoCOFINS(cofins));
        imposto.getContent().add(objectFactory.createTNFeInfNFeDetImpostoICMSUFDest(icmsufDest));

        return imposto;
    }

    /**
     * Prenche Total NFe
     */
    private TNFe.InfNFe.Total preencheTotal(Order order) {
        TNFe.InfNFe.Total total = objectFactory.createTNFeInfNFeTotal();
        TNFe.InfNFe.Total.ICMSTot icmstot = objectFactory.createTNFeInfNFeTotalICMSTot();
        icmstot.setVBC(String.valueOf(order.getTotalAmount()));
        icmstot.setVICMS(String.valueOf(arredondar(order.getTotalAmount() * (icmsValue / 100))));
        icmstot.setVICMSDeson("0.00");
        icmstot.setVFCP("0.00");
        icmstot.setVFCPST("0.00");
        icmstot.setVFCPSTRet("0.00");
        icmstot.setVBCST("0.00");
        icmstot.setVST("0.00");
        icmstot.setVProd(String.valueOf(order.getTotalAmount()));
        icmstot.setVFrete("0.00");
        icmstot.setVSeg("0.00");
        icmstot.setVDesc("0.00");
        icmstot.setVII("0.00");
        icmstot.setVIPI("0.00");
        icmstot.setVIPIDevol("0.00");
        icmstot.setVPIS(String.valueOf(arredondar(order.getTotalAmount() * (pisValue / 100))));
        icmstot.setVCOFINS(String.valueOf(arredondar(order.getTotalAmount() * (cofinsValue / 100))));
        icmstot.setVOutro("0.00");
        icmstot.setVNF(String.valueOf(order.getTotalAmount()));
        total.setICMSTot(icmstot);

        return total;
    }

    /**
     * Preenche Transporte
     */
    private TNFe.InfNFe.Transp preencheTransporte(List<Produto> produtos) {
        TNFe.InfNFe.Transp transp = objectFactory.createTNFeInfNFeTransp();
        transp.setModFrete("0");
        TNFe.InfNFe.Transp.Transporta transporta = objectFactory.createTNFeInfNFeTranspTransporta();
        transporta.setXNome("MEL DISTRIBUTION");
        transp.setTransporta(transporta);
        TNFe.InfNFe.Transp.Vol vol = objectFactory.createTNFeInfNFeTranspVol();
        var peso = produtos.stream().mapToDouble(value -> value.getProPeso().doubleValue()).sum();
        vol.setPesoB(String.valueOf(peso));
        vol.setPesoL(String.valueOf(peso));
        vol.setQVol(String.valueOf(produtos.size()));
        transp.getVol().add(vol);
        return transp;
    }

    /**
     * Preenche dados Pagamento
     */
    private TNFe.InfNFe.Pag preenchePagamento(Double valorPago, Double valorTotal) {
        TNFe.InfNFe.Pag pag = objectFactory.createTNFeInfNFePag();
        TNFe.InfNFe.Pag.DetPag detPag = objectFactory.createTNFeInfNFePagDetPag();
        detPag.setTPag(String.valueOf(valorTotal));
        detPag.setVPag(String.valueOf(valorPago));
        pag.getDetPag().add(detPag);

        return pag;
    }

    public double arredondar(double valor) {
        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
