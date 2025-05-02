package org.eclipse.scout.rt.client.ui.desktop.datachange;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.rt.client.ui.desktop.DesktopEvent;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;

/**
 * Data change manager which may be used to avoid data-change notifications for hidden forms.
 * Events for listeners registered to this manager will be buffered until the form is activated.
 */
@ApplicationScoped
public class ActiveFormDataChangeManager implements IDataChangeListener {

  protected Map<IForm, IDataChangeManager> m_dataChangeManagers = new HashMap<>();

  public ActiveFormDataChangeManager() {
    IDesktop desktop = IDesktop.CURRENT.get();
    desktop.addPropertyChangeListener(IDesktop.PROP_IN_BACKGROUND, e -> onDesktopInBackground((boolean) e.getNewValue()));
    desktop.addDesktopListener(e -> onFormActivate(e.getForm()), DesktopEvent.TYPE_FORM_ACTIVATE);
    desktop.dataChangeListeners().add(this, true);
  }

  @Override
  public void dataChanged(DataChangeEvent event) {
    m_dataChangeManagers.values().forEach(m -> m.fireEvent(event));
  }

  public void add(IForm form, IDataChangeListener listener, boolean weak, Object... dataTypes) {
    m_dataChangeManagers
        .computeIfAbsent(form, this::createDataChangeManager)
        .add(listener, weak, dataTypes);
  }

  public void remove(IDataChangeListener listener) {
    m_dataChangeManagers.values().forEach(m -> m.remove(listener));
  }

  protected IDataChangeManager createDataChangeManager(IForm form) {
    form.addFormListener(e -> onFormClosed(e.getForm()), FormEvent.TYPE_CLOSED);
    return BEANS.get(IDataChangeManager.class);
  }

  protected void onFormActivate(IForm activatedForm) {
    m_dataChangeManagers.forEach((f, m) -> m.setBuffering(f != activatedForm));
  }

  protected void onFormClosed(IForm closedForm) {
    m_dataChangeManagers.remove(closedForm);
  }

  protected void onDesktopInBackground(boolean inBackground) {
    if (!inBackground) {
      m_dataChangeManagers.values().forEach(m -> m.setBuffering(true));
    }
  }
}
