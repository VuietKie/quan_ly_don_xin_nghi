import { useRuntimeConfig } from '#app';
import api from '~/api/config';

export const useApi = () => {
    const config = useRuntimeConfig();
    
    const get = async (url: string, params?: any) => {
        try {
            const response = await api.get(url, { params });
            return response.data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    };

    const post = async (url: string, data?: any) => {
        try {
            const response = await api.post(url, data);
            return response.data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    };

    const put = async (url: string, data?: any) => {
        try {
            const response = await api.put(url, data);
            return response.data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    };

    const del = async (url: string) => {
        try {
            const response = await api.delete(url);
            return response.data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    };

    return {
        get,
        post,
        put,
        delete: del
    };
}; 