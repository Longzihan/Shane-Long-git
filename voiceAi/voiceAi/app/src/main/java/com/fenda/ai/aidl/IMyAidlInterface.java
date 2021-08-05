/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: H:\\workSpace\\voiceAi\\app\\src\\main\\aidl\\com\\fenda\\ai\\aidl\\IMyAidlInterface.aidl
 */
package com.fenda.ai.aidl;
// Declare any non-default types here with import statements

public interface IMyAidlInterface extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IMyAidlInterface {
        private static final String DESCRIPTOR = "com.fenda.ai.aidl.IMyAidlInterface";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.fenda.ai.aidl.IMyAidlInterface interface,
         * generating a proxy if needed.
         */
        public static IMyAidlInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IMyAidlInterface))) {
                return ((IMyAidlInterface) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_registerLuancher: {
                    data.enforceInterface(descriptor);
                    String type = data.readString();
                    LauncherAidlInterface luanAidl = LauncherAidlInterface.Stub.asInterface(data.readStrongBinder());
                    this.registerLauncher(type, luanAidl);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_unRegisterLuancher: {
                    data.enforceInterface(descriptor);
                    String type = data.readString();
                    LauncherAidlInterface luanAidl = LauncherAidlInterface.Stub.asInterface(data.readStrongBinder());
                    this.unRegisterLauncher(type, luanAidl);
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }



        private static class Proxy implements IMyAidlInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void registerLauncher(String type, LauncherAidlInterface aidlInterface) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeStrongBinder(((aidlInterface != null) ? (aidlInterface.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_registerLuancher, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void unRegisterLauncher(String type, LauncherAidlInterface aidlInterface) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeStrongBinder(((aidlInterface != null) ? (aidlInterface.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_unRegisterLuancher, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_registerLuancher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_unRegisterLuancher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public void registerLauncher(String type, LauncherAidlInterface aidlInterface) throws android.os.RemoteException;

    public void unRegisterLauncher(String type, LauncherAidlInterface aidlInterface) throws android.os.RemoteException;
}
