package com.hhrzc.papaya.ppi.core;

/**
 * <b>PPI (Papaya Power Injection) Engine Contract</b>
 * <p>
 * This is a <b>Marker Interface</b>. It does not contain any methods.
 * Extending this interface tells the {@link  com.hhrzc.papaya.ppi.registrar.PapayaRegistrar} (The Scout)
 * that your interface is a valid <b>Papaya Unit</b> ready for ignition.
 * Might be extended in the future to include common methods for all Papaya Units,
 * but for now, it serves as a marker to identify valid Papaya Units for injection.
 * </p>
 *
 * <b>Usage:</b>
 * <pre>
 * &#64;PapayaUnit("login.unit")
 * public interface LoginPage extends PpiRepository {
 *     &#64;Telemetry("${submit-btn}")
 *     LocatorSupplier submitBtn();
 * }
 * </pre>
 *
 * @author PPU Engineering Team
 */
public interface PpiRepository {
    //todo: add in next version: support dynamic default selectors retrieval with parameters, for example:
//    @Telemetry("#{id}")
//    T byId(@Param("id") String id);
//
//    @Telemetry(".{class}")
//    T byClass(@Param("class") String className);
//
//    @Telemetry("[name='{name}']")
//    T byName(@Param("name") String name);
}
