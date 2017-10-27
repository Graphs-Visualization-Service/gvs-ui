package gvs.access;

public enum ProtocolCommand {
  RELEASE_GVS("releaseGVS"), RESERVE_GVS("reserveGVS"), DATA_END(";");

  private final String commandName;

  ProtocolCommand(String commandName) {
    this.commandName = commandName;
  }

  @Override
  public String toString() {
    return commandName;
  }
}
